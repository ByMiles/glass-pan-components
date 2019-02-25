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


/**
 * Element of a {@code FragmentQueue}
 *
 * @author Miles Lorenz
 * @version 1.0
 */
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
        if ((offsetStart <= anElement.offsetStart && offsetEnd >= anElement.offsetStart)
                || (anElement.offsetStart <= offsetStart && anElement.offsetEnd >= offsetStart))
            throw new Exception("INVALID fragments overlap");


        // check if it is before this
        // if => queue it before
        if (offsetStart > anElement.offsetEnd) {
            anElement.queue(previous, this, counter);

            // => if we are not the last pass it
        } else if (next != null) {
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
