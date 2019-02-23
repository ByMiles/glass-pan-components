package de.htw.ai.loz.gpan.lpan.imp.store;

public class FragmentQueueElement {

    private final byte[] fragment;
    private final int offsetStart;
    private final int offsetEnd;

    private FragmentQueueElement previous;
    private FragmentQueueElement next;

    public FragmentQueueElement(byte[] fragment, int offsetStart, int offsetEnd) {
        this.fragment = fragment;
        this.offsetStart = offsetStart;
        this.offsetEnd = offsetEnd;
    }

    public byte[][] addRemainingFragments(byte[][] fragments, int position) {
        fragments[position++] = fragment;
        if (next != null)
            return next.addRemainingFragments(fragments, position);
        return fragments;
    }

    public void tryQueue(FragmentQueueElement anElement, FragmentQueueCounter counter) throws Exception {

        // the fragments would be the same or an exception occurred
        if (offsetStart == anElement.offsetStart && offsetEnd == anElement.offsetEnd) {
            // if they equal it's ignorable, but queueing stops here
            if (fragment.equals(anElement.fragment))
            return;

            throw new Exception("INVALID same fragment offset different payload");
        }

        System.out.println("OVERLAP: " + anElement.offsetStart + " => " + anElement.offsetEnd +
                " | " + offsetStart + " " + offsetEnd);
        // the fragments would overlap ...
        if ((offsetStart <= anElement.offsetStart && offsetEnd >= anElement.offsetStart)
                || (anElement.offsetStart <= offsetStart && anElement.offsetEnd >= offsetStart))
            throw new Exception("INVALID fragments overlap");


        // check if it is before this
        // if => queue it before
        if (offsetStart > anElement.offsetEnd) {
            anElement.queue(previous, this, counter);

            // => if we are not the last pass it
        } else if (next != null){
            next.tryQueue(anElement, counter);

            // => it is the new last and we are not
        } else {
            anElement.queue(this, null, counter);
        }
    }

    public void queue(FragmentQueueElement previous, FragmentQueueElement next, FragmentQueueCounter counter) {

        counter.countFragment(offsetStart, offsetEnd);
        this.previous = previous;
        this.next = next;

        if (previous != null)
            this.previous.next = this;

        if (next != null)
            this.next.previous = this;
    }
}
