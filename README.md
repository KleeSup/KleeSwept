# Library Template using libGDX and Gradle 8.x

Change this to fit your library!

You'll want to edit gradle.properties to match your library's name, description, author, license, and so on.
You probably also want to edit build.gradle to match the projectName and group to what you want to use.

You should "Find in Files" and search for any places that use the word "template" in order to find anything
you will want to replace.

This currently uses Gradle 8.x; if you want an earlier version that uses 7.x,
[here you go](https://github.com/tommyettinger/libgdx-library-template/releases/tag/v7.6)!
Gradle 8.x seems to be fine for library code, and for some applications that don't target Android or iOS.
If you do target Android or iOS with an application, you should probably use Gradle 7.6 until the tooling for those
platforms is updated. Android-specific or iOS-specific libraries may also want to stay on 7.6 .