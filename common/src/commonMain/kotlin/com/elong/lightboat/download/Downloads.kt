package com.elong.lightboat.download

object Downloads {
    /**
     * This download hasn't stated yet
     */
    val STATUS_PENDING = 190

    /**
     * This download has started
     */
    val STATUS_RUNNING = 192

    /**
     * This download has been paused by the owning app.
     */
    val STATUS_PAUSED_BY_APP = 193

    /**
     * This download encountered some network error and is waiting before retrying the request.
     */
    val STATUS_WAITING_TO_RETRY = 194

    /**
     * This download is waiting for network connectivity to proceed.
     */
    val STATUS_WAITING_FOR_NETWORK = 195

    /**
     * This download exceeded a size limit for mobile networks and is waiting for a Wi-Fi
     * connection to proceed.
     */
    val STATUS_QUEUED_FOR_WIFI = 196

    /**
     * This download couldn't be completed due to insufficient storage
     * space.  Typically, this is because the SD card is full.
     */
    val STATUS_INSUFFICIENT_SPACE_ERROR = 198

    /**
     * This download couldn't be completed because no external storage
     * device was found.  Typically, this is because the SD card is not
     * mounted.
     */
    val STATUS_DEVICE_NOT_FOUND_ERROR = 199

    /**
     * This download has successfully completed.
     * Warning: there might be other mStatus values that indicate success
     * in the future.
     * Use isSucccess() to capture the entire category.
     */
    val STATUS_SUCCESS = 200

    /**
     * This request couldn't be parsed. This is also used when processing
     * requests with unknown/unsupported URI schemes.
     */
    val STATUS_BAD_REQUEST = 400

    /**
     * This download can't be performed because the content type cannot be
     * handled.
     */
    val STATUS_NOT_ACCEPTABLE = 406

    /**
     * This download cannot be performed because the length cannot be
     * determined accurately. This is the code for the HTTP error "Length
     * Required", which is typically used when making requests that require
     * a content length but don't have one, and it is also used in the
     * client when a response is received whose length cannot be determined
     * accurately (therefore making it impossible to know when a download
     * completes).
     */
    val STATUS_LENGTH_REQUIRED = 411

    /**
     * This download was interrupted and cannot be resumed.
     * This is the code for the HTTP error "Precondition Failed", and it is
     * also used in situations where the client doesn't have an ETag at all.
     */
    val STATUS_PRECONDITION_FAILED = 412

    /**
     * The lowest-valued error mStatus that is not an actual HTTP mStatus code.
     */
    val MIN_ARTIFICIAL_ERROR_STATUS = 488

    /**
     * The requested destination file already exists.
     */
    val STATUS_FILE_ALREADY_EXISTS_ERROR = 488

    /**
     * Some possibly transient error occurred, but we can't resume the download.
     */
    val STATUS_CANNOT_RESUME = 489

    /**
     * This download was canceled
     */
    val STATUS_CANCELED = 490

    /**
     * This download has completed with an error.
     * Warning: there will be other mStatus values that indicate errors in
     * the future. Use isStatusError() to capture the entire category.
     */
    val STATUS_UNKNOWN_ERROR = 491

    /**
     * This download couldn't be completed because of a storage issue.
     * Typically, that's because the filesystem is missing or full.
     * Use the more specific [.STATUS_INSUFFICIENT_SPACE_ERROR]
     * and [.STATUS_DEVICE_NOT_FOUND_ERROR] when appropriate.
     */
    val STATUS_FILE_ERROR = 492

    /**
     * This download couldn't be completed because of an HTTP
     * redirect response that the download manager couldn't
     * handle.
     */
    val STATUS_UNHANDLED_REDIRECT = 493

    /**
     * This download couldn't be completed because of an
     * unspecified unhandled HTTP code.
     */
    val STATUS_UNHANDLED_HTTP_CODE = 494

    /**
     * This download couldn't be completed because of an
     * error receiving or processing data at the HTTP level.
     */
    val STATUS_HTTP_DATA_ERROR = 495

    /**
     * This download couldn't be completed because of an
     * HttpException while setting up the request.
     */
    val STATUS_HTTP_EXCEPTION = 496

    /**
     * This download couldn't be completed because there were
     * too many redirects.
     */
    val STATUS_TOO_MANY_REDIRECTS = 497

    /**
     * ConnectException
     * SocketTimeoutException
     */
    val STATUS_CONNECT_ERROR = 498

    val HTTP_REQUESTED_RANGE_NOT_SATISFIABLE = 416
    val HTTP_TEMP_REDIRECT = 307
}