 ![image](http://img.blog.csdn.net/20170228141437988?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdnZfYnVn/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
 
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.913453448:GestureContentView:1.0'
	}
