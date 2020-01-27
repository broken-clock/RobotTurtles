// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

import org.springframework.beans.BeansException;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.ObjectPool;
import org.springframework.core.Constants;
import org.apache.commons.pool.PoolableObjectFactory;

public class CommonsPoolTargetSource extends AbstractPoolingTargetSource implements PoolableObjectFactory
{
    private static final Constants constants;
    private int maxIdle;
    private int minIdle;
    private long maxWait;
    private long timeBetweenEvictionRunsMillis;
    private long minEvictableIdleTimeMillis;
    private byte whenExhaustedAction;
    private ObjectPool pool;
    
    public CommonsPoolTargetSource() {
        this.maxIdle = 8;
        this.minIdle = 0;
        this.maxWait = -1L;
        this.timeBetweenEvictionRunsMillis = -1L;
        this.minEvictableIdleTimeMillis = 1800000L;
        this.whenExhaustedAction = 1;
        this.setMaxSize(8);
    }
    
    public void setMaxIdle(final int maxIdle) {
        this.maxIdle = maxIdle;
    }
    
    public int getMaxIdle() {
        return this.maxIdle;
    }
    
    public void setMinIdle(final int minIdle) {
        this.minIdle = minIdle;
    }
    
    public int getMinIdle() {
        return this.minIdle;
    }
    
    public void setMaxWait(final long maxWait) {
        this.maxWait = maxWait;
    }
    
    public long getMaxWait() {
        return this.maxWait;
    }
    
    public void setTimeBetweenEvictionRunsMillis(final long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }
    
    public long getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }
    
    public void setMinEvictableIdleTimeMillis(final long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }
    
    public long getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }
    
    public void setWhenExhaustedActionName(final String whenExhaustedActionName) {
        this.setWhenExhaustedAction(CommonsPoolTargetSource.constants.asNumber(whenExhaustedActionName).byteValue());
    }
    
    public void setWhenExhaustedAction(final byte whenExhaustedAction) {
        this.whenExhaustedAction = whenExhaustedAction;
    }
    
    public byte getWhenExhaustedAction() {
        return this.whenExhaustedAction;
    }
    
    @Override
    protected final void createPool() {
        this.logger.debug("Creating Commons object pool");
        this.pool = this.createObjectPool();
    }
    
    protected ObjectPool createObjectPool() {
        final GenericObjectPool gop = new GenericObjectPool((PoolableObjectFactory)this);
        gop.setMaxActive(this.getMaxSize());
        gop.setMaxIdle(this.getMaxIdle());
        gop.setMinIdle(this.getMinIdle());
        gop.setMaxWait(this.getMaxWait());
        gop.setTimeBetweenEvictionRunsMillis(this.getTimeBetweenEvictionRunsMillis());
        gop.setMinEvictableIdleTimeMillis(this.getMinEvictableIdleTimeMillis());
        gop.setWhenExhaustedAction(this.getWhenExhaustedAction());
        return (ObjectPool)gop;
    }
    
    @Override
    public Object getTarget() throws Exception {
        return this.pool.borrowObject();
    }
    
    @Override
    public void releaseTarget(final Object target) throws Exception {
        this.pool.returnObject(target);
    }
    
    public int getActiveCount() throws UnsupportedOperationException {
        return this.pool.getNumActive();
    }
    
    public int getIdleCount() throws UnsupportedOperationException {
        return this.pool.getNumIdle();
    }
    
    public void destroy() throws Exception {
        this.logger.debug("Closing Commons ObjectPool");
        this.pool.close();
    }
    
    public Object makeObject() throws BeansException {
        return this.newPrototypeInstance();
    }
    
    public void destroyObject(final Object obj) throws Exception {
        this.destroyPrototypeInstance(obj);
    }
    
    public boolean validateObject(final Object obj) {
        return true;
    }
    
    public void activateObject(final Object obj) {
    }
    
    public void passivateObject(final Object obj) {
    }
    
    static {
        constants = new Constants(GenericObjectPool.class);
    }
}
