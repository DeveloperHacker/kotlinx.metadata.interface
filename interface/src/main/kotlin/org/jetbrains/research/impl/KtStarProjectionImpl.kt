package org.jetbrains.research.impl

import org.jetbrains.research.elements.KtType
import org.jetbrains.research.elements.KtTypeArgument


class KtStarProjectionImpl(override val getParent: () -> KtType) : KtTypeArgument.StarProjection()

