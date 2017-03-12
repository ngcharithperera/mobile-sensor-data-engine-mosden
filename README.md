MOSDEN is the Android mobile version of of GSN. Security elements has been removed and a plugin architecture has been introduced. Plugins can be installed separately.

Plugin wrapper has been developed and added to the conf/wrapper.properties.

.aidl files are used to do the binding.

IFunction.aidl contains the main plugin structure.

```
#!android
package au.csiro.gsnlite;

import au.csiro.gsnlite.beans.DataField4Plugins;
import au.csiro.gsnlite.beans.StreamElement4Plugins;

interface IFunction {
  DataField4Plugins[] getDataStructure();
  StreamElement4Plugins[] getReadings();
}

```

However any import chould have thir own aidl files

DataField4Plugins.aidl
```
#!android
package au.csiro.gsnlite.beans;

parcelable DataField4Plugins;

```

StreamElement4Plugins.aidl
```
#!android
package au.csiro.gsnlite.beans;

parcelable StreamElement4Plugins;

```

Related papers:

* Prem Prakash Jayaraman,Charith Perera Dimitrios Georgakopoulos, and Arkady Zaslavsky, MOSDEN: A Scalable Mobile Collaborative Platform for Opportunistic Sensing Applications, Transactions on Collaborative Computing, Volume 14, Issue 1, Pages 1-16

* Prem Prakash Jayaraman, Charith Perera, Dimitrios Georgakopoulos and Arkady Zaslavsky, Efficient Opportunistic Sensing using Mobile Collaborative Platform, Proceedings of the 9th IEEE International Conference on Collaborative Computing: Networking, Applications and Worksharing (COLLABORATECOM), Austin, Texas, United States, October, 2013, Pages 77-86

* Charith Perera, Prem Prakash Jayaraman, Arkady Zaslavsky, Peter Christen, and Dimitrios Georgakopoulos, MOSDEN: An Internet of Things Middleware for Resource Constrained Mobile Devices, Proceedings of the 47th Hawaii International Conference on System Sciences (HICSS), Kona, Hawaii, USA, January, 2014, Pages 1053-1062

