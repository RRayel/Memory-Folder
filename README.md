<h1>Native Memory Manager in Java</h1>

This repository contains a lightweight, thread-safe Native Memory Manager that utilizes Java's sun.misc.Unsafe API. It allows for manual memory management outside of the Java Heap, providing a pool-based allocation system that splits a large native memory block into fixed-size segments.

## Components

The project consists of two primary classes:

* MemoryManager.java: The core engine. It allocates a continuous block of native memory and manages it using a BitSet to track free and occupied blocks.

* MemoryRunner.java: A demonstration utility that initializes the manager, performs an allocation, writes data directly to native memory, and verifies it through a ByteBuffer slice.

## Features
* Off-Heap Allocation: Minimizes GC pressure by storing data in native memory.
* Thread Safety: Uses synchronized blocks for allocation and deallocation to ensure integrity in multi-threaded environments.
* Safety Checks: Includes bounds checking to ensure memory addresses remain within the allocated pool.
* Direct Access: Exposes the Unsafe instance (via reflection) for high-performance direct memory manipulation.

## Implementation
### Memory Structure
* The manager divides total memory into fixed-size blocks. For example:

* Total Memory: 1024 bytes

* Block Size: 64 bytes

* Result: 16 manageable blocks

### Key Methods
* allocate(): Returns the raw long memory address of the next available block. Returns -1 if the pool is full.

* free(long address): Marks the block at the specified address as available for reuse.

* getMemorySlice(long address): Returns a java.nio.ByteBuffer containing a copy of the data at the specific native address.

* shutdown(): Releases the entire native memory block back to the Operating System. Note: This must be called to prevent native memory leaks.
