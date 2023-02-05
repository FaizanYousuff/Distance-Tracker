# Distance tracker


-- Background location permission

If our app is running android 10 or higher we need to declare this permission in manifest file,
in previous version this background permission was not required if we are declaring Coarse or Fine location


- Service is an application component that perform long running operation in background and this does not require user interface

1) Foreground - It perform some operation that is noticeable to user via notification like playing audio file
                 This notification will be visible even if user is in some other app m this cannot be dismissed unless user stops
2) Background - It Performs operation that is not directly noticed by user
3) Bound   - This offers client sever interface , it allows to interact with service these run as long as another application component is bound to it

