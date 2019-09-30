import axios from 'axios'

export function offerAccepted(
    offerId: String,
    version: Number,
    loanProduct: String,
    numberOfInstalment: Number
) : Promise<void> {
    return axios.post("/offer/accept", {
        offerId: offerId,
        offerVersion: version,
        loanProduct: loanProduct,
        numberOfInstalment: numberOfInstalment
    })
}