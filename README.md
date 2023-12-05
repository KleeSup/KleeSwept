# KleeSwept
2D AABB collision detection written in Java

## Features
- swept collision detection between two AABBs
- swept collision detection between multiple AABBs with sorted outcome
- high velocity collision detection
- fixed tunneling problem
- detailed output with hit-position, normal, hit-time, etc.
- chunked world implementation and management

![Alt Text](https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExYmZiNjdmYThmNDZmYzM0NzE2NDUyZmNlY2JlMzdhNTg0YzU2ZDFhMCZlcD12MV9pbnRlcm5hbF9naWZzX2dpZklkJmN0PWc/XrHcgxio3xjnXBAcb5/giphy.gif)

## How to use

```java
//create a new world instance with a fixed chunk size
SimpleCollisionWorld<SweptBody> world = new SimpleCollisionWorld<>(chunkSize);

//initialize world objects
SweptBody player = new SweptBody();
SweptBody obstacle = new SweptBody();

//add them to the world with their bounding box
world.addBody(player, new Rectangle(0,0,10,10));
world.addBody(obstacle, new Rectangle(5,0,10,10));

//retrieving a world objects bounding box
Rectangle playerBoundingBox = world.getBoundingBox(player);
Rectangle obstacleBoundingBox = world.getBoundingBox(obstacle);

CollisionResponse response;
Vector2 displacement = new Vector2(1, 2);
//updating moves the body in the world to the best possible position and retrieves a collision response
response = world.update(player, displacement);
//simulating doesn't actually move the body in the world but still checks for collisions
response = world.simulate(player, displacement);
//forceUpdate forces a teleport/resize in the world (no checks for collisions)
world.forceUpdate(player, playerBoundingBox.x + displacement.x, playerBoundingBox.y + displacement.y);

```

## Implementation
[![](https://jitpack.io/v/KleeSup/KleeSwept.svg)](https://jitpack.io/#KleeSup/KleeSwept)
To implement this library with Gradle and Jitpack, add the following repository to your build.gradle:
```gradle
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```  
Then add the dependency:
```gradle
dependencies {
  implementation 'com.github.KleeSup:KleeSwept:VERSION'
}
```
Note that you need to replace the 'VERSION' part in the dependency with the newest version of the library.
<br>If the library still does not work after the gradle reload, please run the ``buildDependents`` gradle task.</br>

### Copyright 2023 KleeSup

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
