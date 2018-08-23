package io.philarios.layout.example.v0

import io.philarios.canvas.v0.*
import io.philarios.core.v0.Translator
import io.philarios.layout.v0.*

object TestBox : BoxSpec<Any?>({

    key("parent")

    constraints(ConstraintType.LEFT, Scalar {
        value(0.0)
    })

    constraints(ConstraintType.WIDTH, Scalar {
        value(700.0)
    })

    constraints(ConstraintType.TOP, Scalar {
        value(0.0)
    })

    constraints(ConstraintType.HEIGHT, Scalar {
        value(700.0)
    })

    constraints(ConstraintType.TRANSLATE_X, Scalar {
        value(0.0)
    })

    constraints(ConstraintType.TRANSLATE_Y, Scalar {
        value(0.0)
    })

    constraints(ConstraintType.SCALE_X, Scalar {
        value(700.0)
    })

    constraints(ConstraintType.SCALE_Y, Scalar {
        value(700.0)
    })

    children {
        key("child")

        constraints(ConstraintType.LEFT, Scalar {
            value(0.0)
        })

        constraints(ConstraintType.WIDTH, Scalar {
            value(100.0)
        })

        constraints(ConstraintType.TOP, Scalar {
            value(0.0)
        })

        constraints(ConstraintType.HEIGHT, Scalar {
            value(300.0)
        })

        children {
            key("test")

            constraints(ConstraintType.CENTER, Linear {
                partner("child")
                type(ConstraintType.CENTER)
            })

            constraints(ConstraintType.WIDTH, Linear {
                partner("child")
                type(ConstraintType.WIDTH)
            })

            constraints(ConstraintType.MIDDLE, Linear {
                partner("child")
                type(ConstraintType.MIDDLE)
            })

            constraints(ConstraintType.HEIGHT, Linear {
                partner("test")
                type(ConstraintType.WIDTH)
            })

        }

    }

    children {
        key("center")

        constraints(ConstraintType.LEFT, Linear {
            partner("child")
            type(ConstraintType.RIGHT)
            offset(10.0)
        })

        constraints(ConstraintType.WIDTH, Linear {
            partner("child")
            type(ConstraintType.WIDTH)
        })

        constraints(ConstraintType.TOP, Scalar {
            value(0.0)
        })

        constraints(ConstraintType.HEIGHT, Scalar {
            value(300.0)
        })

    }

    children {
        key("square")

        constraints(ConstraintType.CENTER, Linear {
            partner("parent")
            type(ConstraintType.CENTER)
        })

        constraints(ConstraintType.WIDTH, Linear {
            partner("child")
            type(ConstraintType.WIDTH)
        })

        constraints(ConstraintType.MIDDLE, Linear {
            partner("parent")
            type(ConstraintType.MIDDLE)
        })

        constraints(ConstraintType.HEIGHT, Linear {
            partner("square")
            type(ConstraintType.WIDTH)
        })

    }

})

object BoxResolver : Translator<Box, Box> {
    override fun translate(context: Box): Box {
        return resolve(context)
    }
}

data class BoxWithParent(
        val box: Box,
        val parent: BoxWithParent?
)

fun resolve(box: Box): Box {
    val mutableBox = makeMutable(box)
    val boxWithParent = BoxWithParent(mutableBox, null)
    val boxMap = extractBoxMap(boxWithParent)
            .let { it + Pair(boxWithParent.box.key, boxWithParent) }

    boxMap.values.forEach { resolve(it, boxMap) }

    return mutableBox
}

fun makeMutable(box: Box): Box {
    return box.copy(
            children = box.children.map { makeMutable(it) },
            constraints = box.constraints.toMutableMap()
    )
}

fun extractBoxMap(boxWithParent: BoxWithParent): Map<String, BoxWithParent> {
    val boxesWithParent = boxWithParent.box.children
            .map { BoxWithParent(it, boxWithParent) }
            .map { Pair(it.box.key, it) }
            .toMap()

    val childBoxesWithParent = boxesWithParent.values.fold(emptyMap<String, BoxWithParent>()) { map, child ->
        map + extractBoxMap(child)
    }

    return boxesWithParent + childBoxesWithParent
}

fun resolve(boxWithParent: BoxWithParent, boxMap: Map<String, BoxWithParent>) {
    val constraintMap = boxWithParent.box.constraints as MutableMap<ConstraintType, ConstraintValue>

    constraintMap[ConstraintType.TRANSLATE_X] = resolve(boxWithParent, ConstraintType.TRANSLATE_X, boxMap)
    constraintMap[ConstraintType.TRANSLATE_Y] = resolve(boxWithParent, ConstraintType.TRANSLATE_Y, boxMap)
    constraintMap[ConstraintType.SCALE_X] = resolve(boxWithParent, ConstraintType.SCALE_X, boxMap)
    constraintMap[ConstraintType.SCALE_Y] = resolve(boxWithParent, ConstraintType.SCALE_Y, boxMap)
}

fun resolve(boxWithParent: BoxWithParent, type: ConstraintType, boxMap: Map<String, BoxWithParent>): Scalar {
    val (box, parent) = boxWithParent
    val currentValue = box.constraints[type]
    return when (currentValue) {
        is Scalar -> currentValue
        is Linear -> {
            val toKey = currentValue.partner
            val toType = currentValue.type
            val offset = currentValue.offset ?: 0.0
            val multiplier = currentValue.multiplier ?: 1.0

            val toBox = boxMap[toKey]!!
            val toValue = resolve(toBox, toType, boxMap)

            val scalarValue = Scalar(toValue.value * multiplier + offset)
            (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalarValue
            scalarValue
        }
        null -> {
            if (type == ConstraintType.LEFT) {
                if (box.constraints[ConstraintType.WIDTH] != null) {
                    if (box.constraints[ConstraintType.RIGHT] != null) {
                        val width = resolve(boxWithParent, ConstraintType.WIDTH, boxMap)
                        val right = resolve(boxWithParent, ConstraintType.RIGHT, boxMap)
                        val scalar = Scalar(right.value - width.value)
                        (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                        return scalar
                    }
                    if (box.constraints[ConstraintType.CENTER] != null) {
                        val width = resolve(boxWithParent, ConstraintType.WIDTH, boxMap)
                        val center = resolve(boxWithParent, ConstraintType.CENTER, boxMap)
                        val scalar = Scalar(center.value - width.value / 2.0)
                        (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                        return scalar
                    }
                }
                val right = resolve(boxWithParent, ConstraintType.RIGHT, boxMap)
                val center = resolve(boxWithParent, ConstraintType.CENTER, boxMap)
                val scalar = Scalar(center.value * 2.0 - right.value)
                (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                return scalar
            }
            if (type == ConstraintType.WIDTH) {
                val right = resolve(boxWithParent, ConstraintType.RIGHT, boxMap)
                val center = resolve(boxWithParent, ConstraintType.CENTER, boxMap)
                val scalar = Scalar((right.value - center.value) * 2)
                (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                return scalar
            }
            if (type == ConstraintType.RIGHT) {
                val center = resolve(boxWithParent, ConstraintType.CENTER, boxMap)
                val left = resolve(boxWithParent, ConstraintType.LEFT, boxMap)
                val scalar = Scalar(left.value + (center.value - left.value) * 2)
                (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                return scalar
            }
            if (type == ConstraintType.CENTER) {
                val left = resolve(boxWithParent, ConstraintType.LEFT, boxMap)
                val width = resolve(boxWithParent, ConstraintType.WIDTH, boxMap)
                val scalar = Scalar(left.value + width.value / 2.0)
                (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                return scalar
            }

            if (type == ConstraintType.TOP) {
                if (box.constraints[ConstraintType.HEIGHT] != null) {
                    if (box.constraints[ConstraintType.BOTTOM] != null) {
                        val height = resolve(boxWithParent, ConstraintType.HEIGHT, boxMap)
                        val bottom = resolve(boxWithParent, ConstraintType.BOTTOM, boxMap)
                        val scalar = Scalar(bottom.value - height.value)
                        (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                        return scalar
                    }
                    if (box.constraints[ConstraintType.MIDDLE] != null) {
                        val height = resolve(boxWithParent, ConstraintType.HEIGHT, boxMap)
                        val middle = resolve(boxWithParent, ConstraintType.MIDDLE, boxMap)
                        val scalar = Scalar(middle.value - height.value / 2.0)
                        (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                        return scalar
                    }
                }
                val bottom = resolve(boxWithParent, ConstraintType.BOTTOM, boxMap)
                val middle = resolve(boxWithParent, ConstraintType.MIDDLE, boxMap)
                val scalar = Scalar(middle.value * 2.0 - bottom.value)
                (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                return scalar
            }
            if (type == ConstraintType.HEIGHT) {
                val bottom = resolve(boxWithParent, ConstraintType.BOTTOM, boxMap)
                val middle = resolve(boxWithParent, ConstraintType.MIDDLE, boxMap)
                val scalar = Scalar((bottom.value - middle.value) * 2)
                (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                return scalar
            }
            if (type == ConstraintType.BOTTOM) {
                val middle = resolve(boxWithParent, ConstraintType.MIDDLE, boxMap)
                val top = resolve(boxWithParent, ConstraintType.TOP, boxMap)
                val scalar = Scalar(top.value + (middle.value - top.value) * 2)
                (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                return scalar
            }
            if (type == ConstraintType.MIDDLE) {
                val top = resolve(boxWithParent, ConstraintType.TOP, boxMap)
                val height = resolve(boxWithParent, ConstraintType.HEIGHT, boxMap)
                val scalar = Scalar(top.value + height.value / 2.0)
                (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                return scalar
            }

            if (type == ConstraintType.TRANSLATE_X) {
                val left = resolve(boxWithParent, ConstraintType.LEFT, boxMap)
                val parentWidth = resolve(parent!!, ConstraintType.WIDTH, boxMap)
                val scalar = Scalar(left.value / parentWidth.value)
                (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                return scalar
            }
            if (type == ConstraintType.TRANSLATE_Y) {
                val top = resolve(boxWithParent, ConstraintType.TOP, boxMap)
                val parentHeight = resolve(parent!!, ConstraintType.HEIGHT, boxMap)
                val scalar = Scalar(top.value / parentHeight.value)
                (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                return scalar
            }
            if (type == ConstraintType.SCALE_X) {
                val width = resolve(boxWithParent, ConstraintType.WIDTH, boxMap)
                val parentWidth = resolve(parent!!, ConstraintType.WIDTH, boxMap)
                val scalar = Scalar(width.value / parentWidth.value)
                (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                return scalar
            }
            if (type == ConstraintType.SCALE_Y) {
                val height = resolve(boxWithParent, ConstraintType.HEIGHT, boxMap)
                val parentHeight = resolve(parent!!, ConstraintType.HEIGHT, boxMap)
                val scalar = Scalar(height.value / parentHeight.value)
                (box.constraints as MutableMap<ConstraintType, ConstraintValue>)[type] = scalar
                return scalar
            }

            throw Exception()
        }
    }
}

object BoxCanvasRootSpec : CanvasRootSpec<Box>({

    tree(BoxCanvasNodeSpec)

})

object BoxCanvasNodeSpec : CanvasNodeSpec<Box>({

    transform {
        a((context.constraints[ConstraintType.SCALE_X]!! as Scalar).value)
        b(0.0)
        c(0.0)
        d((context.constraints[ConstraintType.SCALE_Y]!! as Scalar).value)
        e((context.constraints[ConstraintType.TRANSLATE_X]!! as Scalar).value)
        f((context.constraints[ConstraintType.TRANSLATE_Y]!! as Scalar).value)
    }
    rectangle(
            (context.constraints[ConstraintType.SCALE_Y]!! as Scalar).value * 255,
            (context.constraints[ConstraintType.TRANSLATE_X]!! as Scalar).value * 255,
            (context.constraints[ConstraintType.TRANSLATE_Y]!! as Scalar).value * 255)

    context.children.forEach {
        include(it) {
            children(BoxCanvasNodeSpec)
        }
    }

})

fun CanvasNodeBuilder<*>.rectangle(red: Double, green: Double, blue: Double) {
    children(CanvasLeaf {
        transform {
            a(1.0);b(0.0);c(0.0);d(1.0);e(0.0);f(0.0)
        }
        include(Color(red, green, blue, 255.0)) { canvas(RectangleCanvas) }
    })
}

object RectangleCanvas : CanvasSpec<Color>({
    path {
        path {
            color {
                red(context.red)
                green(context.green)
                blue(context.blue)
                alpha(255.0)
            }
            verb(MoveTo {
                x(0.0)
                y(0.0)
            })
            verb(LineTo {
                x(1.0)
                y(0.0)
            })
            verb(LineTo {
                x(1.0)
                y(1.0)
            })
            verb(LineTo {
                x(0.0)
                y(1.0)
            })
            verb(LineTo {
                x(0.0)
                y(0.0)
            })
        }
    }
})