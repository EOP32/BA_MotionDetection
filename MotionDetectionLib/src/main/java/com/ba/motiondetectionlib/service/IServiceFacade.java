package com.ba.motiondetectionlib.service;

/**
 * to simplify managing service process in other components
 */
public interface IServiceFacade {
    /**
     * call this method to start service without needing to know which service class it is
     */
    void startDetectionService();

    /**
     * call this method to stop service without needing to know which service class it is
     */
    void stopDetectionService();
}
