# KleeSwept
2D AABB collision detection written in Java

## How to use

```java
//create two AABBs, you can either use SimpleAABB for standalone or RectangleAABB which uses Rectangle from LibGDX
AABB player = new SimpleAABB(0,0,2,2);
AABB collider = new SimpleAABB(4,0,3,3);

//now we define the goal where the player wants to move at. 
//NOTE: always calculate movements based of the CENTER of the AABB as this is where the library calculates collisions.
//in this example, we move by +10 units on the x-axis.
float goalX = player.getCenterX() + 10;
float goalY = player.getCenterY();

//now we can get our result. It contains various information of our collision.
SweptResult result = KleeSweptDetection.checkAABBvsAABB(player, collider, goalX, goalY);
System.out.println(result.isHit);
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
