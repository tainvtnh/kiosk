package com.tnh.kiosk.repositories;

import org.springframework.stereotype.Repository;

import com.tnh.kiosk.configs.StoredProcedureConfig;

import java.util.Map;

@Repository
public class KioskRepository {

    private final StoredProcedureRepository spRepo;

    public KioskRepository(StoredProcedureRepository spRepo) {
        this.spRepo = spRepo;
    }

    /**
     * Gọi stored procedure cho kiosk
     */
    public Map<String, Object> callProcedure(StoredProcedureConfig config, Map<String, Object> params) {
        return spRepo.callProcedure(config, params);
    }
}
