+ Dictionary {

	swapKeys {|aKey, bKey|
		var aSrc, bSrc;

		// find name clashes in dict and swap keys if there are. Otherwise "rename" key.
		aSrc = this[aKey];
		bSrc = this[bKey];

		this[aKey] = bSrc;
		this[bKey] = aSrc;
	}

	changeKeyForValue {|newKey, val|
		var oldName, otherVal;

		// find name clashes in dict and swap keys if there are. Otherwise "rename" key.
		oldName = this.findKeyForValue(val);
		otherVal = this[newKey];

		this[oldName] = otherVal;
		this[newKey] = val;
	}
}

+ Dictionary {
	// atKeys { |keys|
	// 	if (keys.isKindOf(Collection)) {
	// 		^keys.collect (this.at(_))
	// 	};
	// 	^this.at(keys);
	// }
	// now also works for any object as keys in dicts
	// Dictionary[ $a -> 1, $b -> 2, "ab" -> 12 ].atKeys([$a, $b, "abc"]);
	atKeys { |keys|
		^this.at(keys) ?? { keys.collect (this.at(_)) }
	}

}

+ SequenceableCollection {

	// unified method for Array/index, Array of assocs and dicts
	valuesKeysDo { |func|
		if (this.isAssociationArray) {
			this.do { |assoc|
				func.value(assoc.value, assoc.key);
			};
		};
		this.do(func)
	}

	valuesKeysCollect { |func|
		if (this.isAssociationArray) {
			^this.collect { |assoc|
				assoc.key -> func.value(assoc.value, assoc.key);
			};
		};
		^this.collect(func)
	}

	traverseDo { |doAtLeaf, isLeaf, deepKeys, doAtNode|
		var result;
		var canTraverse = { |el| el.mutable and: { el.respondsTo(\traverseDo) } };
		isLeaf = isLeaf ? { |el| (el.mutable and: { el.respondsTo(\traverseDo) }).not };

		this.do { |elem, index|
			var myDeepKeys = deepKeys.asArray ++ index;
			var isAssoc = elem.isKindOf(Association);
			var deeperValue;

			if (isAssoc) { index = elem.key; elem = elem.value };
			if (isLeaf.(elem, myDeepKeys)) {
				doAtLeaf.(elem, myDeepKeys)
			} {
				if (canTraverse.(elem)) {
					result = elem.traverseCollect(doAtLeaf, isLeaf, myDeepKeys);
					doAtNode.value(result, myDeepKeys);
				} {
					"traverseDo - object is malformed:\n"
					"element % at % is not a leaf and not a collection."
					.format(elem, index);
					elem;
				};
			}
		};
	}

	traverseCollect { |doAtLeaf, isLeaf, deepKeys, doAtNode|
		var deeperValue;
		var canTraverse = { |el| el.mutable and: { el.respondsTo(\traverseCollect) } };
		isLeaf = isLeaf ? { |el| (el.mutable and: { el.respondsTo(\traverseCollect) }).not };
		^this.collect { |elem, index|
			var myDeepKeys = deepKeys.asArray ++ index;
			var result, deeperValue;
			var isAssoc = elem.isKindOf(Association);

			if (isAssoc) { index = elem.key; elem = elem.value };
			if (isLeaf.(elem, myDeepKeys)) {
				result = doAtLeaf.(elem, myDeepKeys)
			} {
				if (canTraverse.(elem)) {
					result = elem.traverseCollect(doAtLeaf, isLeaf, myDeepKeys);
					result = doAtNode.value(result, myDeepKeys) ? result;
					result;
				} {
					"traverseDo - object is malformed:\n"
					"element % at % is not a leaf and not a collection."
					.format(elem, index);
					elem
				};
			};
			if (isAssoc) { index -> result } { result }
		};
	}

	unpackAssoc { |value, index|

	}
}
+ Dictionary {

	// unified method for Array/index, Array of assocs and dicts
	valuesKeysDo { |func|
		this.keysValuesDo { |key, val| func.(val, key) }
	}

	valuesKeysCollect { |func|
		^this.collect { |key, val| func.value(val, key) };
	}

	traverseDo { |doAtLeaf, isLeaf, deepKeys, doAtNode|
		var result;
		var canTraverse = { |el| el.mutable and: { el.respondsTo(\traverseDo) } };
		isLeaf = isLeaf ? { |el| (el.mutable and: { el.respondsTo(\traverseDo) }).not };

		this.keysValuesDo { |key, elem|
			var myDeepKeys = deepKeys.asArray ++ key;
			if (isLeaf.(elem)) {
				doAtLeaf.(elem, myDeepKeys)
			} {
				if (canTraverse.(elem)) {
					result = elem.traverseCollect(doAtLeaf, isLeaf, myDeepKeys);
					doAtNode.value(result, myDeepKeys);
				} {
					"traverseDo - object is malformed:\n"
					"element % at % is not a leaf and not a collection."
					.format(elem, key);
					elem
				};
			}
		};
	}

	traverseCollect { |doAtLeaf, isLeaf, deepKeys, doAtNode|
		var deeperValue;
		var canTraverse = { |el| el.mutable and: { el.respondsTo(\traverseDo) } };
		isLeaf = isLeaf ? canTraverse.not;
		^this.collect { |elem, key|
			var deeperValue;
			var result;
			var myDeepKeys = deepKeys.asArray ++ key;
			if (isLeaf.(elem)) {
				result = doAtLeaf.(elem, myDeepKeys)
			} {
				if (canTraverse.(elem)) {
					result = elem.traverseCollect(doAtLeaf, isLeaf, myDeepKeys);
					result = doAtNode.value(result, myDeepKeys) ? result;
				} {
					"traverseCollect - object is malformed: element % at %"
					"is not a leaf and not a traverseable collection."
					.format(elem, key);
					elem
				};
			};
			result
		};
	}
}


