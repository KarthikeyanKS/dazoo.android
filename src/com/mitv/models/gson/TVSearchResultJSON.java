package com.mitv.models.gson;

import java.lang.reflect.Type;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.mitv.Constants;
import com.mitv.enums.ContentTypeEnum;
import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.TVChannel;
import com.mitv.models.TVProgram;
import com.mitv.models.TVSearchResult;
import com.mitv.models.TVSearchResultEntity;
import com.mitv.models.gson.deserializers.TVSearchResultEntityChannelDeserializer;
import com.mitv.models.gson.deserializers.TVSearchResultEntityProgramDeserializer;
import com.mitv.models.gson.deserializers.TVSearchResultEntitySeriesDeserializer;

public class TVSearchResultJSON implements GSONDataFieldValidation, JsonDeserializer<TVSearchResult> {
	@Expose
	protected String displayText;

	@Expose
	protected Float searchScore;

	@Expose
	protected String entityType;

	@Expose(deserialize = false)
	protected TVSearchResultEntity entity;

	public TVSearchResultJSON() {
	}

	@Override
	public TVSearchResult deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		/* Set exposed fields */
		GsonBuilder gsonBuilderSelf = new GsonBuilder();
		gsonBuilderSelf.excludeFieldsWithoutExposeAnnotation();
		Gson gsonSelf = gsonBuilderSelf.create();

		TVSearchResult searchResult = gsonSelf.fromJson(jsonElement, TVSearchResult.class);
		ContentTypeEnum entityTypeEnum = searchResult.getEntityType();

		TVSearchResultEntity entityDependingOnType = null;
		Gson gsonDependingOnType = null;
		GsonBuilder gsonBuilderDependingOnType = new GsonBuilder();

		JsonElement jsonElementEntity = jsonObject.get(Constants.JSON_KEY_SEARCH_RESULT_ITEM_ENTITY);
		switch (entityTypeEnum) {
		case CHANNEL: {
			gsonBuilderDependingOnType.registerTypeAdapter(TVSearchResultEntity.class, new TVSearchResultEntityChannelDeserializer());
			break;
		}
		case PROGRAM: {
			gsonBuilderDependingOnType.registerTypeAdapter(TVSearchResultEntity.class, new TVSearchResultEntityProgramDeserializer());
			break;
		}
		case SERIES: {
			gsonBuilderDependingOnType.registerTypeAdapter(TVSearchResultEntity.class, new TVSearchResultEntitySeriesDeserializer());
			break;
		}
		}

		gsonDependingOnType = gsonBuilderDependingOnType.create();
		entityDependingOnType = gsonDependingOnType.fromJson(jsonElementEntity, TVSearchResultEntity.class);
		searchResult.entity = entityDependingOnType;

		return searchResult;
	}

	public ContentTypeEnum getEntityType() {
		return ContentTypeEnum.getContentTypeEnumFromStringRepresentation(entityType);
	}

	public String getDisplayText() {
		return displayText;
	}

	public Float getSearchScore() {
		return searchScore;
	}

	public TVSearchResultEntity getEntity() {
		return entity;
	}

	@Override
	public boolean areDataFieldsValid() {
		boolean areFieldsValid = (displayText != null && entityType != null && entityType.length() > 0 && entity != null);

		if (areFieldsValid) {
			TVSearchResultEntity searchResultEntity = getEntity();
			switch (getEntityType()) {
			case CHANNEL: {
				TVChannel tvChannel = searchResultEntity.getChannel();
				areFieldsValid = (tvChannel.areDataFieldsValid());
				break;
			}
			case SERIES: {
				String seriesId = searchResultEntity.getId();
				String seriesName = searchResultEntity.getName();
				areFieldsValid = (!TextUtils.isEmpty(seriesId) && !TextUtils.isEmpty(seriesName));
				break;
			}
			case PROGRAM: {
				TVProgram program = searchResultEntity.getProgram();
				areFieldsValid = (program.areDataFieldsValid());
				break;
			}
			}

		}

		return areFieldsValid;
	}
}
