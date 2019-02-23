package de.htw.ai.loz.gpan.lpan.imp.store;

import de.htw.ai.loz.gpan.lpan.msg.FragmentedPacket;

import java.util.concurrent.locks.ReentrantLock;

import static de.htw.ai.loz.gpan.lpan.header.FragHeader.*;

public class FragmentQueue {

    private FragmentQueueCounter counter;
    private FragmentQueueElement head;
    private boolean isComplete;
    private ReentrantLock lock;

    public FragmentQueue() {
        System.out.println("QUEUE NEW");
        this.lock = new ReentrantLock();
    }

    public FragmentedPacket tryQueueGetComplete(byte[] fragment) throws Exception {

        try {
            lock.lock();

            if (isComplete)
                head = null;

            if (head == null)
                startQueue(fragment);
            else
                queueNext(fragment);

            if (counter.isComplete())
                return createFragmentedDatagram();
            return null;
        } catch (Exception e) {
            System.out.println("Exception on fragmentQueue: " + e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    private FragmentedPacket createFragmentedDatagram() {

        FragmentedPacket datagram = new FragmentedPacket();
        byte[][] fragments = new byte[counter.getFragmentCount()][];

        datagram.setFragmentBytes(head.addRemainingFragments(fragments, 0));
        datagram.setDatagramSize(counter.getDatagramSize());
        return datagram;
    }

    private void startQueue(byte[] fragment) {
        isComplete = false;
        int size = getDatagramSize(fragment);

        counter = new FragmentQueueCounter(size);

        head = createElement(fragment);
        head.queue(null, null, counter);
    }

    private FragmentQueueElement createElement(byte[] fragment) {
        if (isFirstFragment(fragment)) {
            return new FragmentQueueElement(fragment, 0,fragment.length - 5);
        } else {
            int offset = getOffset(fragment);
            return new FragmentQueueElement(fragment, offset, offset + fragment.length - 6);
        }
    }

    private void queueNext(byte[] fragment) throws Exception {
        FragmentQueueElement anElement = createElement(fragment);
        head.tryQueue(anElement, counter);
    }
}
