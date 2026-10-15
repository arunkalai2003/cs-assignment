package com.company.shortener.application.service;

import com.company.shortener.api.dto.request.CreateShortUrlRequest;
import com.company.shortener.api.dto.request.UpdateShortUrlRequest;
import com.company.shortener.api.dto.response.ShortUrlCreateResponse;
import com.company.shortener.api.dto.response.ShortUrlReadResponse;
import com.company.shortener.api.dto.response.ShortUrlUpdateResponse;
import com.company.shortener.domain.exception.ServiceException;

public interface UrlShortenerService {

    /**
     * Creates a new short URL record.
     *
     * Expected behavior:
     * - validates the request data before saving
     * - rejects invalid URLs, duplicate short codes, or blank creator metadata
     * - generates a code when none is provided
     * - returns the created entity as a create response payload
     * - throws ServiceException when the request is invalid or the data cannot be persisted
     */
    ShortUrlCreateResponse createShortUrl(CreateShortUrlRequest request);

    /**
     * Retrieves a short URL by its public short code.
     *
     * Expected behavior:
     * - returns the link details when the code exists and is active
     * - throws ServiceException with NOT_FOUND when the code does not exist
     * - throws ServiceException with EXPIRED when the record has expired
     * - throws ServiceException with INACTIVE when the record is disabled
     */
    ShortUrlReadResponse getShortUrlByCode(String shortCode);

    /**
     * Updates an existing short URL record.
     *
     * Expected behavior:
     * - updates only the supplied fields
     * - validates the new values before saving
     * - throws ServiceException with NOT_FOUND when the target short code is missing
     * - throws ServiceException when the update would create a duplicate short code or invalid data
     */
    ShortUrlUpdateResponse updateShortUrl(String shortCode, UpdateShortUrlRequest request);

    /**
     * Deletes or deactivates a short URL record.
     *
     * Expected behavior:
     * - removes or disables the record according to the system lifecycle rule
     * - should fail with NOT_FOUND when the short code does not exist
     * - should not allow deletion of already inactive records unless explicit reactivation is supported
     * - should record the actor performing the delete when supplied
     */
    void deleteShortUrl(String shortCode, String deletedBy);

    /**
     * Resolves the original URL for a short code and prepares a redirect response.
     *
     * Expected behavior:
     * - returns the original destination URL when the short code exists, is active, and not expired
     * - throws ServiceException with NOT_FOUND for missing codes
     * - throws ServiceException with EXPIRED for expired records
     * - throws ServiceException with INACTIVE for disabled records
     * - the controller or caller is responsible for issuing the HTTP redirect
     */
    String redirect(String shortCode);
}
