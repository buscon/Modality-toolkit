/*

/* todo:
* proper indexing of 8x8 pads,
* labels for buttons:
- up, down left right buttons,
- fader ctrl: volume, pan, send, device
- sceneLaunch: ...
- gui layout of all of them
*/

a = MKtl('apc1', "akai-apcmini"); a.gui;
a.elementAt(\sl).action_({ |elem| [elem.name, elem.value].postln; });
a.elementAt(\keyOn).action_({ |elem| [\ON, elem.name, elem.value].postln; });
a.elementAt(\keyOff).action_({ |elem| [\OFF, elem.name, elem.value].postln; });
*/
(
deviceName: "APC MINI",
protocol: 'midi',
deviceType: \launchpad,
elementTypes: [\fader, \pad, \button],
status: (
	linux: "unknown",
	osx: "tested desc only, looks ok. 2016-03-17, adc",
	win: "unknown"),

idInfo: "APC MINI",

deviceInfo: (
	vendorURI: "http://www.akaipro.com/product/apc-mini",
	manualURI: "http://6be54c364949b623a3c0-4409a68c214f3a9eeca8d0265e9266c0.r0.cf2.rackcdn.com/990/documents/APC%20mini%20-%20User%20Guide%20-%20v1.0.pdf",
	// description: "",
	features: [
		"64 buttons with tri-lighting",
		"9 faders",
		"17 buttons",
	],
	longName: "AKAI APCmini",
),
elementsDesc: (

	elements: [
		(
			key: \pad,
			shared: (elementType: 'pad',
				midiChan: 0, spec: 'midiNote', ioType: \inout),
			elements: (0..63).collect { |midiNote, i|
				MKtlDesc.notePair(
					(i+1).asSymbol, midiNote,
					shared: (
						style: (
							row: 7 - (i div: 8),
							column: i % 8,
							showLabel: true
						)
					)
				)
			}
		),
		(
			key: \navBut,
			shared: (elementType: 'button',
				midiChan: 0, spec: 'midiBut', ioType: \inout),
			elements: (64..67).collect { |midiNote, i|
				MKtlDesc.notePair(
					(i+1).asSymbol, midiNote,
					shared: (
						style: (
							row: 8,
							column: i,
							showLabel: true
						)
					)
				)
			}
		),
		(
			key: \ctrlBut,
			shared: (elementType: 'button',
				midiChan: 0, spec: 'midiBut', ioType: \inout),
			elements: (68..71).collect { |midiNote, i|
				MKtlDesc.notePair(
					(i+1).asSymbol, midiNote,
					shared: (
						style: (
							row: 8,
							column: i + 4,
							showLabel: true
						)
					)
				)
			}
		),
		(
			key: \launchBut,
			shared: (elementType: 'button',
				midiChan: 0, spec: 'midiBut', ioType: \inout),
			elements: (82..89).collect { |midiNote, i|
				MKtlDesc.notePair(
					(i+1).asSymbol, midiNote,
					shared: (
						style: (
							row: i,
							column: 8,
							showLabel: true
					))
				)
			}
		),
		MKtlDesc.notePair(
			\shift, 98,
			shared: (
				\elementType: 'button',
				\midiChan: 0,
				\spec: 'midiBut',
				\ioType: \inout,
				style: (
					row: 8,
					column: 8,
					showLabel: true
				)
			)
		),
		(
			key: \sl,
			shared: (midiMsgType: 'cc', elementType: 'slider',
				midiChan: 0, spec: 'midiBut', ioType: \in),
			elements: (48..56).collect { |midinote|
				(midiNum: midinote, spec: 'midiCC', ioType: \in,
					\style: (row: 9, column: i, showLabel: true)
				)
			}
		)
	]
)
)
