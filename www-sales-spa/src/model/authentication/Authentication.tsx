import Keycloak from 'keycloak-js';
import {Cookies} from "../../infrastructure/Cookies";

class AuthenticationTemplate {
    private token: string | undefined = ""
    private keycloak = Keycloak("/keycloak.json");

    private _onAuthenticated = () => {}
    private _onError = (error: string) => {}

    onAuthenticated(onAuthenticated: () => void): AuthenticationTemplate {
        this._onAuthenticated = onAuthenticated

        return this
    }

    onError(onError: (error: string) => void): AuthenticationTemplate {
        this._onError = onError

        return this
    }

    isAuthenticated(): boolean {
        return this.keycloak.authenticated || false
    }

    getToken() : string | undefined {
        return this.keycloak.token;
    }

    logout() {
        this.keycloak.logout()
    }

    applyAuth() {
        this.keycloak
            .init({
                onLoad: 'login-required',
                checkLoginIframe: false
            })
            .success(
                authenticated => {
                    this.token = this.keycloak.token
                    Cookies.set("jwt-token", this.token)

                    this._onAuthenticated()
                })
            .error(
                error => {
                    this.token = this.keycloak.token
                    Cookies.set("jwt-token", this.token)

                    if (error) {
                        this._onError(error.error)
                    } else {
                        this._onError("error " + JSON.stringify(error))
                    }
                }
            );
    }
}

export const AUTHENTICATION = new AuthenticationTemplate()