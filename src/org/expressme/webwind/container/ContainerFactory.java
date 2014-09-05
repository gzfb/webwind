package org.expressme.webwind.container;

import java.util.List;

import org.expressme.webwind.Config;

/**
 * Factory instance for creating IoC container.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public interface ContainerFactory {

    /**
     * Init container factory.
     */
    void init(Config config);

    /**
     * Find all beans in container.
     */
    List<Object> findAllBeans();

    /**
     * When container destroyed.
     */
    void destroy();
}
