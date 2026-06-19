package com.tnh.kiosk.services;

import com.tnh.kiosk.properties.KioskProperties;
import com.tnh.kiosk.repositories.QueryRepository;
import com.tnh.kiosk.repositories.StoredProcedureRepository;
import jakarta.persistence.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class KioskService {

    private static final Logger logger = LoggerFactory.getLogger(KioskService.class);

    private final QueryRepository queryRepo;
    private final StoredProcedureRepository spRepo;
    private final Map<String, KioskProperties.QueryConfig> queryConfigs;
    private final Map<String, KioskProperties.StoredProcedureConfig> spConfigs;

    public KioskService(QueryRepository queryRepo,
                        StoredProcedureRepository spRepo,
                        KioskProperties kioskProperties) {
        this.queryRepo = queryRepo;
        this.spRepo = spRepo;
        this.queryConfigs = kioskProperties.getQueries();
        this.spConfigs = kioskProperties.getStoredProcedures();
    }

    public List<Tuple> executeQuery(String key, Map<String, Object> params) {
        KioskProperties.QueryConfig config = queryConfigs.get(key);
        if (config == null) {
            logger.error("Query key not found: {}", key);
            throw new IllegalArgumentException("No query config for key: " + key);
        }

        logger.info("Executing query with key: {} and params: {}", key, params);
        List<Tuple> result = queryRepo.getData(config.getSql(), params);
        logger.info("Query result count: {}", result.size());

        return result;
    }

    public Map<String, Object> executeProcedure(String key, Map<String, Object> params) {
        KioskProperties.StoredProcedureConfig config = spConfigs.get(key);
        if (config == null) {
            logger.error("Stored procedure key not found: {}", key);
            throw new IllegalArgumentException("No SP config for key: " + key);
        }

        logger.info("Executing stored procedure with key: {} and params: {}", key, params);

        // Convert config sang spRepo
        com.tnh.kiosk.configs.StoredProcedureConfig spConfig = new com.tnh.kiosk.configs.StoredProcedureConfig();
        spConfig.setName(config.getName());
        spConfig.setInParams(config.getInParams());
        spConfig.setOutParams(config.getOutParams());

        Map<String, Object> result = spRepo.callProcedure(spConfig, params);
        //logger.info("Stored procedure result: {}", result);

        return result;
    }

    public List<String> getInParams(String key) {
        KioskProperties.StoredProcedureConfig config = spConfigs.get(key);
        if (config != null) {
            return config.getInParams(); // Trả về danh sách maHoSo, maNb... từ file yml
        }
        return new ArrayList<>();
    }
}
