# PostersApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createPoster**](PostersApi.md#createPoster) | **POST** /api/posters | Crée un nouveau poster |
| [**deletePoster**](PostersApi.md#deletePoster) | **DELETE** /api/posters/{id} | Supprime un poster |
| [**getAllPosters**](PostersApi.md#getAllPosters) | **GET** /api/posters | Liste tous les posters |
| [**getPosterById**](PostersApi.md#getPosterById) | **GET** /api/posters/{id} | Récupère un poster par son id |
| [**updatePoster**](PostersApi.md#updatePoster) | **PUT** /api/posters/{id} | Modifie un poster existant |


<a id="createPoster"></a>
# **createPoster**
> Poster createPoster(poster)

Crée un nouveau poster

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PostersApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PostersApi apiInstance = new PostersApi(defaultClient);
    Poster poster = new Poster(); // Poster | 
    try {
      Poster result = apiInstance.createPoster(poster);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PostersApi#createPoster");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **poster** | [**Poster**](Poster.md)|  | |

### Return type

[**Poster**](Poster.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Poster créé avec succès |  -  |
| **400** | Données invalides (champs manquants) |  -  |
| **409** | Un poster avec cet id existe déjà |  -  |
| **500** | Erreur serveur |  -  |

<a id="deletePoster"></a>
# **deletePoster**
> deletePoster(id)

Supprime un poster

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PostersApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PostersApi apiInstance = new PostersApi(defaultClient);
    String id = "tt0050083"; // String | Identifiant du poster (ex. tt0050083)
    try {
      apiInstance.deletePoster(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling PostersApi#deletePoster");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Identifiant du poster (ex. tt0050083) | |

### Return type

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Poster supprimé avec succès |  -  |
| **404** | Poster introuvable |  -  |
| **500** | Erreur serveur |  -  |

<a id="getAllPosters"></a>
# **getAllPosters**
> List&lt;Poster&gt; getAllPosters()

Liste tous les posters

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PostersApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PostersApi apiInstance = new PostersApi(defaultClient);
    try {
      List<Poster> result = apiInstance.getAllPosters();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PostersApi#getAllPosters");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;Poster&gt;**](Poster.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Liste recuperée avec succes |  -  |
| **500** | Erreur serveur |  -  |

<a id="getPosterById"></a>
# **getPosterById**
> Poster getPosterById(id)

Récupère un poster par son id

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PostersApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PostersApi apiInstance = new PostersApi(defaultClient);
    String id = "tt0050083"; // String | Identifiant du poster (ex. tt0050083)
    try {
      Poster result = apiInstance.getPosterById(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PostersApi#getPosterById");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Identifiant du poster (ex. tt0050083) | |

### Return type

[**Poster**](Poster.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Poster récupéré avec succès |  -  |
| **404** | Poster introuvable |  -  |
| **500** | Erreur serveur |  -  |

<a id="updatePoster"></a>
# **updatePoster**
> Poster updatePoster(id, posterPatch)

Modifie un poster existant

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PostersApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    PostersApi apiInstance = new PostersApi(defaultClient);
    String id = "tt0050083"; // String | Identifiant du poster (ex. tt0050083)
    PosterPatch posterPatch = new PosterPatch(); // PosterPatch | 
    try {
      Poster result = apiInstance.updatePoster(id, posterPatch);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PostersApi#updatePoster");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Identifiant du poster (ex. tt0050083) | |
| **posterPatch** | [**PosterPatch**](PosterPatch.md)|  | |

### Return type

[**Poster**](Poster.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Poster mis à jour avec succès |  -  |
| **404** | Poster introuvable |  -  |
| **500** | Erreur serveur |  -  |

