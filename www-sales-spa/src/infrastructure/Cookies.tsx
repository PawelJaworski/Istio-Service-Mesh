export const Cookies = {
    set(name: string, val: string | undefined) {
        const date = new Date();
        const value = val;

        date.setTime(date.getTime() + (7 * 24 * 60 * 60 * 1000));
        document.cookie = name+"="+value+"; expires="+date.toUTCString()+"; path=/";
    },
    get(name: string) {
        const value = "; " + document.cookie;
        const parts = value.split("; " + name + "=");

        if (parts.length == 2) {
            // @ts-ignore
            return parts.pop().split(";").shift();
        }
    },
    delete(name: string) {
        const date = new Date();

        date.setTime(date.getTime() + (-1 * 24 * 60 * 60 * 1000));
        document.cookie = name+"=; expires="+date.toUTCString()+"; path=/";
    }
}