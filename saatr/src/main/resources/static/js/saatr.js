;
(function(undefined) {

	'use strict';

	if (typeof (Saatr) === 'object') {
		return;
	}

	window.Saatr = {

		copyToClipboard : function(event, text) {
			window.prompt("Copy to clipboard: Ctrl+C, Enter", text);
			event.preventDefault();
			event.stopPropagation();
			return false;
		}
	};

})();
