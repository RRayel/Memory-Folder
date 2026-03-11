public class MemoryRunner {
    public static void main(String[] args) {
        // total memory = 1024 bytes, block size = 64 bytes → 16 blocks
        MemoryManager memoryManager = new MemoryManager(1024, 64);

        try {
            // Allocate one memory block
            long address = memoryManager.allocate();
            if (address == -1) {
                System.err.println(" Failed to allocate memory block");
                return;
            }
            System.out.println(" Allocated block at address: " + address);

            // Write some bytes to the allocated block using Unsafe
            for (int i = 0; i < 10; i++) {
                MemoryManager.UNSAFE.putByte(address + i, (byte) (65 + i)); // 'A', 'B', 'C', ...
            }

            // Retrieve a ByteBuffer view of the memory slice
            var buffer = memoryManager.getMemorySlice(address);

            // Read the first few bytes from the slice
            System.out.print(" Memory contents: ");
            for (int i = 0; i < 10; i++) {
                System.out.print((char) buffer.get(i));
            }
            System.out.println();

            // Free the allocated block
            memoryManager.deallocate(address);
            System.out.println(" Memory block deallocated.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Always release the native memory pool
            memoryManager.shutdown();
            System.out.println(" Memory Manager shutdown complete.");
        }
    }
}
