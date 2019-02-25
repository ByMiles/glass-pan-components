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

import java.time.Duration;
import java.time.LocalTime;

/**
 * Helper class for the fragment queue.
 * <ul>
 *     <li>counts fragments</li>
 *     <li>monitors tag and size</li>
 * </ul>
 * @author Miles Lorenz
 * @version 1.0
 */
public class FragmentQueueCounter {

    private int fragmentCount;
    private int datagramSize;
    private int currentSize;


    private LocalTime lastArrive;

    public FragmentQueueCounter(int datagramSize) {
        this.datagramSize = datagramSize;
        fragmentCount = 0;
        currentSize = 0;
        lastArrive = LocalTime.MIN;
    }

    public void countFragment(int offsetStart, int offsetEnd) {
        currentSize += offsetEnd - offsetStart + 1;
        fragmentCount++;
        lastArrive = LocalTime.now();
    }

    public boolean isComplete() {
        return datagramSize == currentSize;
    }

    public boolean isTimeOut(int allowedSeconds) {
        return Duration.between(lastArrive, LocalTime.now()).getSeconds() < allowedSeconds;
    }

    public int getFragmentCount() {
        return fragmentCount;
    }

    public int getDatagramSize() {
        return datagramSize;
    }
}
