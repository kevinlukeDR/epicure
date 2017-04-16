function showStreamAtVideoElement(stream, videoElementId, callback) {

  var videoElement = document.getElementById(videoElementId);
  if ('srcObject' in videoElement) {
    videoElement.srcObject = stream;
    console.log(videoElement.paused);
    videoElement.play();
  } else {
    videoElement.src = URL.createObjectURL(stream);
  }

  if (callback) {
    callback(stream);
  }

}