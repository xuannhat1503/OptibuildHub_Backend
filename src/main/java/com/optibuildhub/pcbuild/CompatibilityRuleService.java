package com.optibuildhub.pcbuild;

import com.optibuildhub.part.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompatibilityRuleService {

    private final PartSpecParser specParser;

    public CompatibilityResult check(List<Part> parts) {
        List<String> warns = new ArrayList<>();

        // Nhóm theo category để lấy nhanh
        Map<String, List<Part>> byCat = parts.stream()
                .collect(Collectors.groupingBy(p -> p.getCategory().toUpperCase()));

        Part cpu  = first(byCat, "CPU");
        Part main = first(byCat, "MAIN");
        Part ram  = first(byCat, "RAM");
        Part gpu  = first(byCat, "GPU");
        Part psu  = first(byCat, "PSU");
        Part pcase= first(byCat, "CASE");
        Part cooler = first(byCat, "COOLER");

        PartSpec cpuSpec = spec(cpu);
        PartSpec mainSpec= spec(main);
        PartSpec ramSpec = spec(ram);
        PartSpec gpuSpec = spec(gpu);
        PartSpec psuSpec = spec(psu);
        PartSpec caseSpec= spec(pcase);
        PartSpec coolerSpec= spec(cooler);

        // 1) Socket CPU ↔ Mainboard
        if (cpu != null && main != null) {
            if (!safeEq(cpuSpec.getSocket(), mainSpec.getSocket())) {
                warns.add("Socket CPU và Mainboard không khớp");
            }
        }

        // 2) RAM type & slots
        if (ram != null && main != null) {
            if (!safeEq(ramSpec.getRamType(), mainSpec.getRamType())) {
                warns.add("RAM type khác với Mainboard (ví dụ DDR4 vs DDR5)");
            }
            if (mainSpec.getRamSlots() != null && mainSpec.getRamSlots() < 1) {
                warns.add("Mainboard không đủ khe RAM");
            }
        }

        // 3) PSU wattage
        int cpuTdp = opt(cpuSpec.getCpuTdp());
        int gpuTdp = opt(gpuSpec.getGpuTdp());
        int baseWatt = cpuTdp + gpuTdp;
        int headroom = (int)Math.round(baseWatt * 0.25); // 25% headroom
        int needWatt = baseWatt + headroom;
        int psuWatt = opt(psuSpec.getPsuWatt());
        if (psu != null && psuWatt > 0 && needWatt > 0 && psuWatt < needWatt) {
            warns.add("PSU công suất thấp: cần tối thiểu ~" + needWatt + "W");
        }

        // 4) Form factor Mainboard ↔ Case
        if (main != null && pcase != null) {
            String ffMain = val(mainSpec.getFormFactor());
            String ffCase = val(caseSpec.getFormFactor());
            // đơn giản: nếu case form factor nhỏ hơn main → cảnh báo
            if (!ffMain.isEmpty() && !ffCase.isEmpty()) {
                if (!ffCase.contains(ffMain)) { // tuỳ format bạn lưu, có thể cần map ATX/MATX/ITX
                    warns.add("Form factor Mainboard có thể không khớp Case (" + ffMain + " vs " + ffCase + ")");
                }
            }
        }

        // 5) GPU length ↔ Case
        if (gpu != null && pcase != null) {
            Integer gpuLen = gpuSpec.getGpuLengthMm();
            Integer caseGpuMax = caseSpec.getCaseGpuMaxMm();
            if (gpuLen != null && caseGpuMax != null && gpuLen > caseGpuMax) {
                warns.add("GPU quá dài so với Case (GPU " + gpuLen + "mm > Case " + caseGpuMax + "mm)");
            }
        }

        // 6) Cooler height ↔ Case
        if (cooler != null && pcase != null) {
            Integer coolerH = coolerSpec.getCoolerHeightMm();
            Integer caseCoolerMax = caseSpec.getCaseCoolerMaxMm();
            if (coolerH != null && caseCoolerMax != null && coolerH > caseCoolerMax) {
                warns.add("Tản nhiệt CPU quá cao so với Case ("
                        + coolerH + "mm > " + caseCoolerMax + "mm)");
            }
        }

        // 7) Slots: M.2 / SATA / PCIe (đơn giản)
        // Bạn có thể đếm số lượng STORAGE/M.2 yêu cầu; ở đây chỉ cảnh báo nếu mainSpec thiếu dữ liệu
        if (main != null) {
            if (mainSpec.getM2Slots() != null && mainSpec.getM2Slots() == 0) {
                warns.add("Mainboard không có khe M.2 (cần kiểm tra nếu bạn dùng SSD NVMe)");
            }
        }

        // Tổng giá & watt
        BigDecimal totalPrice = parts.stream()
                .map(p -> Optional.ofNullable(p.getPrice()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalWatt = parts.stream()
                .mapToInt(p -> Optional.ofNullable(p.getWattage()).orElse(0))
                .sum();
        // Nếu wattage null ở CPU/GPU, ta vẫn dùng cpuTdp + gpuTdp làm tham chiếu:
        totalWatt = Math.max(totalWatt, baseWatt);

        boolean compatible = warns.isEmpty();
        // compute recommended PSU
        Integer recommended = computeRecommendedPsuWatt(parts, cpuSpec, gpuSpec);

        return CompatibilityResult.builder()
            .compatible(compatible)
            .warnings(warns)
            .totalPrice(totalPrice)
            .totalWatt(totalWatt)
            .recommendedPsuWatt(recommended)
            .build();
    }

    private Part first(Map<String, List<Part>> byCat, String cat) {
        var list = byCat.get(cat);
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    private PartSpec spec(Part p) {
        return p == null ? new PartSpec() : specParser.parse(p.getSpecJson());
    }

    private boolean safeEq(String a, String b) {
        return val(a).equalsIgnoreCase(val(b));
    }

    private String val(String s) { return s == null ? "" : s.trim(); }

    private int opt(Integer v) { return v == null ? 0 : v; }

    private Integer computeRecommendedPsuWatt(List<Part> parts, PartSpec cpuSpec, PartSpec gpuSpec) {
        int cpuTdp = opt(cpuSpec.getCpuTdp());
        int gpuTdp = opt(gpuSpec.getGpuTdp());

        // Sum explicit wattage for other parts (exclude CPU/GPU)
        int other = parts.stream()
                .filter(p -> {
                    String c = p.getCategory() == null ? "" : p.getCategory().toUpperCase();
                    return !("CPU".equals(c) || "GPU".equals(c));
                })
                .mapToInt(p -> Optional.ofNullable(p.getWattage()).orElse(0))
                .sum();

        // If other is zero (no explicit wattage provided), use conservative estimate
        if (other == 0) other = 50; // baseline for motherboard, drives, fans

        int totalLoad = cpuTdp + gpuTdp + other;

        // Add headroom (25%)
        double headroomFactor = 1.25;
        double needWithHeadroom = totalLoad * headroomFactor;

        // Efficiency factor (choose rating slightly above required output)
        double efficiencyFactor = 0.88; // assumes 88% effective
        int recommended = (int) Math.ceil(needWithHeadroom / efficiencyFactor);

        // Round up to nearest 50W
        recommended = ((recommended + 49) / 50) * 50;
        return recommended;
    }
}