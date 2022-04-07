package Domain.Recommendation.KNN;

import java.util.concurrent.locks.ReentrantLock;

import Domain.Item;

/**
 *
 * @author Pablo Jose Galvan Calderon
 */
public class ItemAffinity implements Comparable<ItemAffinity> {
    private Item item;
    private double affinitySum = 0;
    private double weightSum = 0;
    private ReentrantLock accumulateLock;
    
    public ItemAffinity(Item item) {
        this.item = item;
        this.accumulateLock = new ReentrantLock();
    }
    
    public void addAffinity(double newAffinity, double weight) {
        affinitySum += newAffinity;
        weightSum += weight;
    }
    
    public Item getItem() {
        return item;
    }
    
    public double getAffinity() {
        if (weightSum == 0) return -1;
        return affinitySum / weightSum;
    }
    
    public void accumulateAffinity(ItemAffinity other) {
        accumulateLock.lock();
        addAffinity(other.affinitySum, other.weightSum);
        accumulateLock.unlock();
    }
    
    @Override
    public int compareTo(ItemAffinity o) {
        return Double.compare(o.getAffinity(), getAffinity());
    }
}
