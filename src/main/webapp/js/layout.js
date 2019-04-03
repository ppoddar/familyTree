
const HORIZONTAL = 1
const VERTICAL   = 2

class Graph {
	constructor(data) {
		if (!data) throw 'no data'
		var nodes = data['nodes'] || []
		var links = data['links'] || []
		this.nodes = new Map()
		this.relations = []

		for (var i = 0; i < nodes.length; i++) {
			var node = nodes[i]
			this.nodes.set(node.id,  new Node(node))
		}
		for (var i = 0; i < links.length; i++) {
			var link = links[i]
			var source = this.getNode(link.source)
			var target = this.getNode(link.target)
			var rel = new Relationship(source, link.rel, target)
			this.relations.push(rel);
			if (link.rel == 'child') {
			    source.addChild(target)
			    if (source.male)
			    	   	target.father = source
			    	else
			    		target.mother = source
			    		
			} else if (link.rel == 'spouse') {
				source.spouse = target
				target.spouse = source
			}
		}
	}
	
	/**
	 * get nodes without a parent
	 */
	getRoots() {
		var roots = []
		this.nodes.forEach(function(v,k,m) {
			if (v.father == null && v.mother == null) roots.push(v)
		})
		return roots
	}
	
	getNode(id) {
		if (this.nodes.has(id)) {
			return this.nodes.get(id)
		}
		throw 'node ' + id + ' not found'
	}
}

class TreeLayout {
	/**
	 * renders a graph by adding it to given div
	 */
	render($parent, graph) {
		var roots = graph.getRoots()
		console.log('roots ' + roots)
		for (var i = 0; i < roots.length; i++) {
			roots[i].render($parent)
		}
	}
}
/**
 * a node in a graph. Holds data to display and HTML element 
 * properties
 */
class Node {
	/**
	 * creates a node
     * @param data contains name, dob, photo url etc.
     * @param children list of child nodes can be null for leaf nodes 
	 */
	constructor(data) {
		this.data = data
		this.father = null
		this.mother = null
		this.spouse = null
		this.children = []
	}
	
	addChild(child) {
		if (typeof child == 'Node') 
			throw child + ' is not a Node. It is ' + typeof child
		this.children.push(child)
	}
	
	/**
	 * computes bounding box. 
	 * bounding box is bounding box of all children combined horizontally
	 * and then combined vertically with this node
	 * 
	 */
	computeBoundingBox () {
		if (!!this.box) return this.box;
		console.log('computeBoundingBox [' + this + ']')
		this.box = this.rawSize();
		if (this.children == null) return this.box;
		var subtree = new BoundingBox(0,0,0,0)
		for (var i = 0; i < this.children.length; i++) {
			var child = this.children[i]
			subtree = subtree.combine(child.computeBoundingBox(), HORIZONTAL)
		}
		this.box = this.box.combine(subtree, VERTICAL)
		console.log('computed bounding box for [' + this + '] =' + this.box)
		return this.box
	}
	/**
	 * size without children
	 */
	rawSize() {
		var size = new Size(100, 200)
		return new BoundingBox(0,0, size.w, size.h)
	}
	
	/** create a div that shows all data (name, photo etc.)
	  * div is not attached to anything. but its position
	  * and size are set
	  */ 
	render($parent) {
		var $container = $('<div>');
		$parent.append($container)

	    var $card = $('<div>')
		$card.css('position', 'relative')
	    $container.append($card)
		var $title = $('<p>')
		$title.text(this.data['name'])
		$card.append($title)
		
		var hasChild = this.children.length>0
		if (hasChild) {
			var $table = $('<div>')
			$container.append($table)
			$table.css('position', 'relative')
			$table.css('width', '100%')
			$table.css('display', 'table')
			var $row = $('<div>')
			$table.append($row)
			$row.css('display', 'table-row')
			for (var i = 0; i < this.children.length; i++) {
				var $childDiv = this.children[i].render($row);
				$row.append($childDiv)
				$childDiv.css('display','table-cell')
			}
			var shift = $table.width()/2 + 'px'
			console.log('shift card [' + this + '] by ' + shift)
			$card.css('left',  shift )
//			$container.css('border', '1px solid red')
//			$table.css('border', '1px solid blue')
		}
//		this.printElement('container ', $container)
//		this.printElement('card', $card)
//		this.printElement('children table', $table)
//			
//		$card.css('border', '1px solid green')
//		console.log('node ' + this + ' size ' + $container.width() 
//				+ 'x' + $container.height()
//				+ ' has  child?' + hasChild)
		
		return $container
	}
	
	printElement(name, e) {
		console.log('size of ' + name + ' = ' + e.width() + ' X ' + e.height())
	}
}

/**
 * a bounding box
 */
class BoundingBox {
	/**
	 * create with left-top and width and height
	 */
	constructor(x,y,w,h) {
		this.x = x
		this.y = y
		this.w = w
		this.h = h
	}
	
	/**
	 * combine with another horizontally or vertically
	 */
	combine(other, dir) {
		if (other == undefined || other == null) {
			return new BoundingBox(this.x, this.y, this.w, this.h)
		}
		if (dir == HORIZONTAL) {
			return new BoundingBox(
					Math.min(this.x, other.x), 
					Math.min(this.y, other.y), 
					(this.w+other.w), 
					Math.max(this.h, other.h))
		} else if (dir == VERTICAL) {
			return new BoundingBox(
					Math.min(this.x, other.x), 
					Math.min(this.y, other.y), 
					Math.max(this.w+other.w), 
					this.h + other.h)
		}
	}
	/**
	 * align other w.r.t this 
	 * @param side TOP, BOTTOM, LEFT, RIGHT, MID-HORIZONTAL, MID-VERTICAL
	 */
	align(other, side) {
		if (side == TOP) {
			other.y = this.y
		} else if (side == BOTTOM) {
			other.y = this.y + (this.h - other.h)
		}
	}
	
	
}

/**
 * width and height of something
 */
class Size {
	constructor (w,h) {
		this.w = w
		this.h = h
	}
}

class Relationship {
	constructor(source, rel, target) {
		this.source = source
		this.target = target
		this.rel = rel
	}
}

BoundingBox.prototype.toString = function() {
	return this.x + ',' + this.y + ' [' + this.w + 'x' + this.h + ']'
}

Node.prototype.toString = function() {
	return this.data['name']
}




