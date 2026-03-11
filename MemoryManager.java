import sun.misc.Unsafe;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.BitSet;

public class MemoryManager {
    static final Unsafe UNSAFE = getUnsafe();// This is obtained via reflection because the standard public method throws a SecurityException
    private final long memoryAddress;
    private final BitSet freeBlocks;
    private final int blockSize;//the fixed size of each memory block in bytes
    private final int numBlocks;//number of blocks in the memory pool

    public MemoryManager(int totalMemorySize, int blockSize) {
        if (totalMemorySize % blockSize != 0) {
            throw new IllegalArgumentException("Total memory size must be a multiple of block size.");
        }
        this.memoryAddress = UNSAFE.allocateMemory(totalMemorySize);
        this.blockSize = blockSize;
        this.numBlocks = totalMemorySize / blockSize;
        this.freeBlocks = new BitSet(numBlocks);
        this.freeBlocks.set(0, numBlocks); // Mark all blocks as free
    }

    
    public synchronized long allocate() {
        // Find the index of the next available (free) block.
        int blockIndex = freeBlocks.nextSetBit(0);
        if (blockIndex < 0) {
            return -1; // No free blocks available
        }
        freeBlocks.clear(blockIndex);
        // Calculate and return the raw memory address for the allocated block
        // using pointer arithmetic from the base address.
        return memoryAddress + (long) blockIndex * blockSize;
    }

    /**
     * Deallocates a previously allocated block of memory
     * This method is synchronized for thread safety
     */
    public synchronized void free(long address) {
        int blockIndex = (int) ((address - memoryAddress) / blockSize);
        if (blockIndex >= 0 && blockIndex < numBlocks) {
            freeBlocks.set(blockIndex); // Mark the block as free
        }
    }

    /**
     * Shuts down the allocator and releases the large block of native memory
     * This must be called explicitly to prevent a native memory leak
     */
    public void shutdown() {
        // Use the native `Unsafe.freeMemory()` method to release the entire
        // native memory block back to the operating system
        UNSAFE.freeMemory(memoryAddress);
    }

    private static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Unsafe instance", e);
        }
    }


    public ByteBuffer getMemorySlice(long address) {
    if (address < memoryAddress || address >= memoryAddress + (long) numBlocks * blockSize) {
        throw new IllegalArgumentException("Address out of range");
    }

    ByteBuffer buffer = ByteBuffer.allocate(blockSize);
    for (int i = 0; i < blockSize; i++) {
        byte value = UNSAFE.getByte(address + i);
        buffer.put(i, value);
    }
    return buffer;
}

public void deallocate(long address) {
    free(address);
}

}
