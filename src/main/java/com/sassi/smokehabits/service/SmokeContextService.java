package com.sassi.smokehabits.service;

import com.sassi.smokehabits.dto.request.SmokeContextRequest;
import com.sassi.smokehabits.dto.response.SmokeContextResponse;
import com.sassi.smokehabits.entity.SmokeContext;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.repository.SmokeContextRepository;
import com.sassi.smokehabits.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SmokeContextService {
    private static final Logger log = LoggerFactory.getLogger(SmokeContextService.class);
    private SmokeContextRepository smokeContextRepository;
    private UserRepository userRepository;

    public SmokeContextService(SmokeContextRepository smokeContextRepository, UserRepository userRepository) {
        this.smokeContextRepository = smokeContextRepository;
        this.userRepository = userRepository;
    }

    public List<SmokeContext> findAllByUUID(UUID userId) {
           User user = userRepository.findById(userId).orElseThrow();
           return smokeContextRepository.findAllByUser(user);
    }

    public SmokeContextResponse createSmokeContext(UUID userId, SmokeContextRequest smokeContextRequest) {
        User user = userRepository.findById(userId).orElseThrow();

        SmokeContext smokeContext = new SmokeContext();

        smokeContext.setContext(smokeContextRequest.getContext());
        smokeContext.setUser(user);
        smokeContext.setColorUI(smokeContextRequest.getColorUI());

        smokeContextRepository.save(smokeContext);

        return smokeContext.toSmokeContextResponse();


    }

    public SmokeContext editSmokeContext(SmokeContext smokeContextToEdit,  SmokeContextRequest smokeContextRequest) {
        smokeContextToEdit.setColorUI(smokeContextRequest.getColorUI());
        smokeContextToEdit.setContext(smokeContextRequest.getContext());
        smokeContextRepository.save(smokeContextToEdit);
        return smokeContextToEdit;
    }


}
