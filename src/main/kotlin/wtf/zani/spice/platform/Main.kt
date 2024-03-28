package wtf.zani.spice.platform

import wtf.zani.spice.transformers.LwjglTransformer
import wtf.zani.spice.transformers.OptifineTransformer

internal fun init() {
    TransformerRegistry += LwjglTransformer()
    TransformerRegistry += OptifineTransformer()
}
