# LikeApi

All URIs are relative to *https://virtserver.swaggerhub.com/IGORTON/AlbumStore/1.2*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getLikes**](LikeApi.md#getLikes) | **GET** /review/{albumID} | 
[**review**](LikeApi.md#review) | **POST** /review/{likeornot}/{albumID} | 

<a name="getLikes"></a>
# **getLikes**
> Likes getLikes(albumID)



get likes or dislikes for  album

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.LikeApi;


LikeApi apiInstance = new LikeApi();
String albumID = "albumID_example"; // String | albumID
try {
    Likes result = apiInstance.getLikes(albumID);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling LikeApi#getLikes");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **albumID** | **String**| albumID |

### Return type

[**Likes**](Likes.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: applications/json, application/json

<a name="review"></a>
# **review**
> review(likeornot, albumID)



like or dislike album

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.LikeApi;


LikeApi apiInstance = new LikeApi();
String likeornot = "likeornot_example"; // String | like or dislike album
String albumID = "albumID_example"; // String | albumID
try {
    apiInstance.review(likeornot, albumID);
} catch (ApiException e) {
    System.err.println("Exception when calling LikeApi#review");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **likeornot** | **String**| like or dislike album |
 **albumID** | **String**| albumID |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

