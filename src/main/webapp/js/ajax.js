function callServer() {
	var url = "family/Poddars"
	$.ajax({
		url: url,
		success: function(data) {
			console.log('received from server ' + url)
			console.log(data)
		}
		
	})
	
}