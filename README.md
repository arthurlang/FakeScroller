# FakeScroller
simulate scroll ways:
1,adb shell input swipe (root permissions
2,view.dispatchTouchEvent motionevent (small region)
3,InputManager.injectInputEvent(system signituer & )
4，scrollView/NestedScrollView/Recyclerview.scrollBy(0,100) (call View api)

FakeScroller simulate injectInputEvent
