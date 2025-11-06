package org.example.axon.dto.data;

import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicInfo {
    private String id;
    private String name;
    private String address;
}
