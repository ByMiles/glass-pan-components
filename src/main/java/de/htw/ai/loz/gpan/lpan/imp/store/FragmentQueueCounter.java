package de.htw.ai.loz.gpan.lpan.imp.store;

import java.time.Duration;
import java.time.LocalTime;

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
        System.out.println("COUNTED: " + offsetStart + " => " + offsetEnd );
        currentSize += offsetEnd - offsetStart + 1;
        fragmentCount++;
        lastArrive = LocalTime.now();
    }

    public boolean isComplete() {
        System.out.println("COMPLETE? : " + datagramSize + " " + currentSize);
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
