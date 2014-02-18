package com.millicom.mitv.models.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.millicom.mitv.interfaces.DeserializeManuallyAnnotation;

@DeserializeManuallyAnnotation
public class DeserializeManuallyAnnotationExclusionStrategy implements ExclusionStrategy {
	public boolean shouldSkipClass(Class<?> clazz) {
		boolean shouldSkipClass = clazz.getAnnotation(DeserializeManuallyAnnotation.class) != null;
		return shouldSkipClass;
	}

	public boolean shouldSkipField(FieldAttributes f) {
		boolean shouldSkipField = f.getAnnotation(DeserializeManuallyAnnotation.class) != null;
		return shouldSkipField;
	}
}