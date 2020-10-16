package com.livanov.demo.integrationtesting.domain;

import com.livanov.demo.integrationtesting.domain.ports.CachedIpDetailsService;
import com.livanov.demo.integrationtesting.domain.ports.IpDetailsRepository;
import com.livanov.demo.integrationtesting.domain.ports.RemoteIpDetailsService;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IpDetailsServiceFromDb extends IpDetailsThirdPartyService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(IpDetailsServiceFromDb.class);

    public IpDetailsServiceFromDb(RemoteIpDetailsService remoteService,
                                  IpDetailsRepository repository,
                                  CachedIpDetailsService cachedService) {
        super(repository, cachedService, remoteService);
    }

    public IpDetails getInfo(String ip) {

        IpDetails ipDetails = tryGetFromDb(ip)
                .orElse(super.getInfo(ip));

        log.info("IpDetails [{}]     retrieved from Database.", ip);

        trySaveToCache(ipDetails);

        return ipDetails;
    }

    private Optional<IpDetails> tryGetFromDb(String ip) {
        try {
            return repository.findOneByIp(ip);
        } catch (Exception ex) {
            log.warn("IpDetails [" + ip + "] NOT retrieved from Database.");
            return Optional.empty();
        }
    }

}
