package com.livanov.demo.integrationtesting.domain;

import com.livanov.demo.integrationtesting.domain.ports.CachedIpDetailsService;
import com.livanov.demo.integrationtesting.domain.ports.IpDetailsRepository;
import com.livanov.demo.integrationtesting.domain.ports.RemoteIpDetailsService;
import org.slf4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
public class IpDetailsServiceFromCache extends IpDetailsServiceFromDb {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(IpDetailsServiceFromCache.class);

    IpDetailsServiceFromCache(RemoteIpDetailsService remoteService,
                              IpDetailsRepository repository,
                              CachedIpDetailsService cachedService) {
        super(remoteService, repository, cachedService);
    }

    public IpDetails getInfo(String ip) {

        IpDetails ipInfo = tryGetFromCache(ip).orElse(super.getInfo(ip));
        log.info("IpDetails [{}]     retrieved from Cache.", ip);
        return ipInfo;
    }

    private Optional<IpDetails> tryGetFromCache(String ip) {
        try {
            return cachedService.getInfo(ip);
        } catch (Exception ex) {
            log.warn("IpDetails [" + ip + "] NOT retrieved from Cache.");
            return Optional.empty();
        }
    }


}
