$(document).ready(function() {
    $('#registrationForm').bootstrapValidator({
        // To use feedback icons, ensure that you use Bootstrap v3.1.0 or later
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            email: {
                validators: {
                    notEmpty: {
                        message: 'The email address is required and cannot be empty'
                    },
                    emailAddress: {
                        message: 'The email address is not a valid'
                    }
                }
            },
            pwd: {
                validators: {
                    notEmpty: {
                        message: 'The password is required and cannot be empty'
                    },
                    identical: {
                        field: 'pwdB',
                        message: 'The password and its confirm are not the same'
                    }
                }
            },
            pwdB: {
                validators: {
                    notEmpty: {
                        message: 'The password is required and cannot be empty'
                    },
                    identical: {
                        field: 'pwd',
                        message: 'The password and its confirm are not the same'
                    }
                }
            },
            fname: {
                validators: {
                    notEmpty: {
                        message: 'A first name is required and cannot be empty'
                    }
                }
            },
            lname: {
                validators: {
                    notEmpty: {
                        message: 'A last name is required and cannot be empty'
                    }
                }
            },
            cname: {
                validators: {
                    notEmpty: {
                        message: 'A company name is required and cannot be empty'
                    }
                }
            },
            contract: {
                validators: {
                    callback: {
                        message: 'For non USDOT employees contract or agreement information is required',
                        callback: function(value, validator, $field) {
                            if ( $("#email").val().toLowerCase().endsWith("@dot.gov") ) {
                                return true;
                            }
                            return value && value.length > 0 && value.trim().length > 0;
                        }
                    }
                }
			}            
        }
    })
    .on('success.field.bv', function(e, data) {
        if (data.bv.isValid()) {
            data.bv.disableSubmitButtons(false);
        }
    });

    $("#registrationForm").on('reset', function() {
        $('#registrationForm').data('bootstrapValidator').resetForm(true);
        grecaptcha.reset();
    });
});