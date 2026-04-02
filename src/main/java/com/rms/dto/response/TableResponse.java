package com.rms.dto.response;
import com.rms.entity.RestaurantTable;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TableResponse {
    private Long id; private String tableNumber; private Integer capacity;
    private String section; private String status;
    public static TableResponse from(RestaurantTable t){
        return TableResponse.builder().id(t.getId()).tableNumber(t.getTableNumber())
            .capacity(t.getCapacity()).section(t.getSection()).status(t.getStatus().name()).build(); }
}
