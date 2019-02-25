/*
Copyright 2019 Miles Lorenz

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package de.htw.ai.loz.gpan.lpan.imp.store;

import de.htw.ai.loz.gpan.lpan.msg.FragmentedPacket;

import java.util.concurrent.locks.ReentrantLock;

import static de.htw.ai.loz.gpan.lpan.header.FragHeader.*;

/**
 * Stores fragments of IPv6 packets and composes them if they are complete
 * @author Miles Lorenz
 * @version 1.0
 */
public class FragmentQueue {

    private FragmentQueueCounter counter;
    private FragmentQueueElement head;
    private boolean isComplete;
    private ReentrantLock lock;

    public FragmentQueue() {
        this.lock = new ReentrantLock();
    }

    /**
     * Stores fragments of a IPv6 packet and returns it as it is complete
     * @param fragment The data unit to store
     * @return A packet as bundle of fragments or null if it was not complete.
     * @throws Exception if there is an invalidation.
     */
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
