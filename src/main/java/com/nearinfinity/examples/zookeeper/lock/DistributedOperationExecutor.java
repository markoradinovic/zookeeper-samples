package com.nearinfinity.examples.zookeeper.lock;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

public class DistributedOperationExecutor {

    private ZooKeeper zk;

    public DistributedOperationExecutor(ZooKeeper zk) {
        this.zk = zk;
    }

    public static List<ACL> DEFAULT_ACL = ZooDefs.Ids.OPEN_ACL_UNSAFE;

    public void withLock(String name, String lockPath, DistributedOperation op)
            throws InterruptedException, KeeperException {
        internalWithLock(name, lockPath, DEFAULT_ACL, op);
    }

    public void withLock(String name, String lockPath, List<ACL> acl, DistributedOperation op)
            throws InterruptedException, KeeperException {
        internalWithLock(name, lockPath, acl, op);
    }

    private void internalWithLock(String name, String lockPath, List<ACL> acl, DistributedOperation op)
            throws InterruptedException, KeeperException {
        BlockingWriteLock lock = new BlockingWriteLock(name, zk, lockPath, acl);
        try {
            lock.lock();
            op.execute();
        }
        finally {
            lock.unlock();
        }
    }

}
