package cc.sukazyo.cono.morny.core.assets

import cc.sukazyo.restools.utils.PathsHelper

/** Used to define images.
  *
  *
  * @example
  *
  * {{{
  * object MyImages extends AssetsImageSpec {
  *
  *     // define the namespace for the images in this object.
  *     assetsNamespace = "my-group"
  *
  *     // define an image. This image will be located at "<assets-root>/my-group/images/logo.png".
  *     val myLogo = png("logo")
  *
  *     // for non-png images, use `image` method instead.
  *     // -> "<assets-root>/my-group/images/splash.jpg"
  *     val mySplash = image("splash", "jpg")
  *
  *     // define with an alternative namespace is also supported.
  *     // -> "<assets/root>/friend-group/images/logo.png"
  *     val friendsLogo = image("friend-group", "logo", "png")
  *
  *     // image name can contain path, which will be resolved to subdirectories.
  *     // -> "<assets-root>/my-group/images/icons/close.png"
  *     val closeIcon = png("icons/close")
  *
  * }
  * }}}
  */
trait AssetsImageSpec {
	
	protected def assetsNamespace: String
	
	protected def image (namespace: String, filename: String, ext: String): AssetImage = {
		AssetImage(namespace, PathsHelper.parseString(s"$filename.$ext").toList)
	}
	
	protected def image (filename: String, ext: String): AssetImage =
		image (this.assetsNamespace, filename, ext)
	
	protected def png (filename: String): AssetImage =
		this.image(filename, "png")
	
}
