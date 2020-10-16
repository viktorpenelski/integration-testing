package com.livanov.demo.integrationtesting.domain;

import com.livanov.demo.integrationtesting.domain.ports.CachedIpDetailsService;
import com.livanov.demo.integrationtesting.domain.ports.IpDetailsRepository;
import com.livanov.demo.integrationtesting.domain.ports.RemoteIpDetailsService;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IpDetailsThirdPartyService extends IpDetailsService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(IpDetailsThirdPartyService.class);
    private final RemoteIpDetailsService remoteService;

    public IpDetailsThirdPartyService(IpDetailsRepository repository,
                                      CachedIpDetailsService cachedService,
                                      RemoteIpDetailsService remoteService) {
        super(repository, cachedService);
        this.remoteService = remoteService;
    }

    public IpDetails getInfo(String ip) {

        IpDetails ipDetails = tryGetFromThirdParty(ip)
                .orElseThrow(() -> new IpDetailsNotFoundException(ip));

        log.info("IpDetails [{}]     retrieved from Third Party Provider Service.", ip);

        trySaveToDb(ipDetails);
        trySaveToCache(ipDetails);

        return ipDetails;
    }

    private Optional<IpDetails> tryGetFromThirdParty(String ip) {
        try {
            return remoteService.getInfo(ip);
        } catch (Exception ex) {
            log.warn("IpDetails [" + ip + "] NOT retrieved from Third Party Provider.");
            return Optional.empty();
        }
    }
}
