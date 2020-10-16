package com.livanov.demo.integrationtesting.domain;

import com.livanov.demo.integrationtesting.domain.ports.CachedIpDetailsService;
import com.livanov.demo.integrationtesting.domain.ports.IpDetailsRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Service
public abstract class IpDetailsService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(IpDetailsService.class);

    protected final IpDetailsRepository repository;
    protected final CachedIpDetailsService cachedService;

    public IpDetailsService(IpDetailsRepository repository,
                            CachedIpDetailsService cachedService) {
        this.repository = repository;
        this.cachedService = cachedService;
    }

    public abstract IpDetails getInfo(String ip);

    public List<IpDetails> getAll() {

        Spliterator<IpDetails> all = repository.findAll().spliterator();

        return StreamSupport.stream(all, false)
                .collect(toList());
    }

    protected void trySaveToCache(IpDetails ipDetails) {
        try {
            cachedService.cache(ipDetails);
            log.debug("IpDetails [{}]     saved to Cache.", ipDetails.getIp());
        } catch (Exception ex) {
            log.warn("IpDetails [" + ipDetails.getIp() + "] NOT saved to Cache.");
        }
    }

    protected void trySaveToDb(IpDetails ipDetails) {
        try {
            repository.save(ipDetails);
            log.debug("IpDetails [{}]     saved to Database.", ipDetails.getIp());
        } catch (Exception ex) {
            log.warn("IpDetails [" + ipDetails.getIp() + "] NOT saved to Database.");
        }
    }
}
