package com.rms.service;
import com.rms.dto.request.TableRequest;
import com.rms.dto.response.TableResponse;
import com.rms.entity.RestaurantTable;
import java.util.List;
public interface TableService {
    TableResponse createTable(TableRequest req);
    TableResponse updateTable(Long id, TableRequest req);
    TableResponse updateStatus(Long id, RestaurantTable.TableStatus status);
    List<TableResponse> getAllTables();
    List<TableResponse> getAvailableTables();
    TableResponse getTableById(Long id);
    void deleteTable(Long id);
}
