package com.optibuildhub.pcbuild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartSpec {
    private String socket;          // CPU / MAIN
    private String ramType;         // DDR4 / DDR5
    private Integer ramSlots;       // MAIN
    private Integer ramMaxGb;       // MAIN
    private String formFactor;      // ATX / MATX / ITX (MAIN, CASE)
    private Integer gpuLengthMm;    // GPU
    private Integer coolerHeightMm; // COOLER
    private Integer caseGpuMaxMm;   // CASE
    private Integer caseCoolerMaxMm;// CASE
    private Integer psuWatt;        // PSU output
    private Integer cpuTdp;         // CPU TDP
    private Integer gpuTdp;         // GPU TDP
    private Integer m2Slots;        // MAIN
    private Integer sataPorts;      // MAIN
    private Integer pcieSlots;      // MAIN
}