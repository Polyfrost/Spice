package wtf.zani.spice.platform

import wtf.zani.spice.transformers.LwjglTransformer

internal fun init() {
    TransformerRegistry += LwjglTransformer()
}
