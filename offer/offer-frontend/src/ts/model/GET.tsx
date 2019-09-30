export function nextOfferVersion() : Promise<number> {
    return Promise.resolve(new Date().getTime())
}

export function nextOfferId() : Promise<String> {
    const now = new Date()
    return Promise.resolve("offer-" + now.getMonth() + now.getHours() + now.getMinutes() + now.getSeconds() + now.getMilliseconds())
}