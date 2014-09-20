/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\ResearchCode\\MOSDEN\\MOSDEN\\src\\au\\csiro\\gsnlite\\IFunction.aidl
 */
package au.csiro.gsnlite;
public interface IFunction extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements au.csiro.gsnlite.IFunction
{
private static final java.lang.String DESCRIPTOR = "au.csiro.gsnlite.IFunction";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an au.csiro.gsnlite.IFunction interface,
 * generating a proxy if needed.
 */
public static au.csiro.gsnlite.IFunction asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof au.csiro.gsnlite.IFunction))) {
return ((au.csiro.gsnlite.IFunction)iin);
}
return new au.csiro.gsnlite.IFunction.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getDataStructure:
{
data.enforceInterface(DESCRIPTOR);
au.csiro.gsnlite.beans.DataField4Plugins[] _result = this.getDataStructure();
reply.writeNoException();
reply.writeTypedArray(_result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
return true;
}
case TRANSACTION_getReadings:
{
data.enforceInterface(DESCRIPTOR);
au.csiro.gsnlite.beans.StreamElement4Plugins[] _result = this.getReadings();
reply.writeNoException();
reply.writeTypedArray(_result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements au.csiro.gsnlite.IFunction
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public au.csiro.gsnlite.beans.DataField4Plugins[] getDataStructure() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
au.csiro.gsnlite.beans.DataField4Plugins[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDataStructure, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArray(au.csiro.gsnlite.beans.DataField4Plugins.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public au.csiro.gsnlite.beans.StreamElement4Plugins[] getReadings() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
au.csiro.gsnlite.beans.StreamElement4Plugins[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getReadings, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArray(au.csiro.gsnlite.beans.StreamElement4Plugins.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getDataStructure = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getReadings = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public au.csiro.gsnlite.beans.DataField4Plugins[] getDataStructure() throws android.os.RemoteException;
public au.csiro.gsnlite.beans.StreamElement4Plugins[] getReadings() throws android.os.RemoteException;
}
