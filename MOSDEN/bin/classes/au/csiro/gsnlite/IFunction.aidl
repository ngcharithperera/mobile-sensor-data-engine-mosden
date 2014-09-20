package au.csiro.gsnlite;

import au.csiro.gsnlite.beans.DataField4Plugins;
import au.csiro.gsnlite.beans.StreamElement4Plugins;

interface IFunction {
  DataField4Plugins[] getDataStructure();
  StreamElement4Plugins[] getReadings();
}
